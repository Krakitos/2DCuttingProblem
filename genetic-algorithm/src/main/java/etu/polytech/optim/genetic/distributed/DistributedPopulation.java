package etu.polytech.optim.genetic.distributed;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.Population;
import etu.polytech.optim.genetic.lang.populations.FixedSizePopulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Morgan on 06/05/2015.
 */
public class DistributedPopulation implements Population{

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker DISPATCH_MARKER = MarkerManager.getMarker("[DISPATCHING]");
    private static final Marker HANDLING_MARKER = MarkerManager.getMarker("[HANDLING]");

    private static final int DEFAULT_PORT = 15151;

    private final InetAddress MULTICAST_ADDRESS;

    private final Population population;
    private final DistributedServer server;

    public DistributedPopulation(Population population) throws IOException {
        MULTICAST_ADDRESS = InetAddress.getByName("224.100.100.100");

        this.population = population;
        this.server = new DistributedServer(DEFAULT_PORT);

        new Thread(server, "Distributed Server Thread").start();
    }

    public DistributedPopulation(int size) throws IOException {
        this(new FixedSizePopulation(size));
    }

    @Override
    public void addChromosome(@NotNull Chromosome chromosome) {
        try {
            Chromosome fittest = fittestChromosome();
            if (population.size() > 0 && chromosome.fitness() < fittest.fitness()){
                server.sendNewSolution(chromosome);
            }
        } catch (IOException e) {
            LOGGER.warn("Unable to send the new chromosome", e);
        }finally {
            population.addChromosome(chromosome);
        }
    }

    @Override
    public Chromosome getRandom() {
        return population.getRandom();
    }

    @Override
    public boolean removeChromosome(@NotNull Chromosome chromosome) {
        return population.removeChromosome(chromosome);
    }

    @Override
    public Chromosome fittestChromosome() {
        return population.fittestChromosome();
    }

    @Override
    public int size() {
        return population.size();
    }

    @Override
    public Map<String, Long> hits() {
        return population.hits();
    }

    /**
     * Class handling the communication protocol
     */
    private class DistributedServer implements Runnable{

        private static final int BUFFER_SIZE = 16 * 1024;

        private static final byte SOLUTION_MASK = 1;

        private MulticastSocket socket;

        public DistributedServer(int port) throws IOException {
            socket = new MulticastSocket(port);
            socket.joinGroup(MULTICAST_ADDRESS);
        }

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            try {
                for(;;) {

                    SocketAddress sender = socket.getChannel().receive(buffer);
                    if (buffer.hasRemaining()) {
                        LOGGER.info(HANDLING_MARKER, "Received packet with {} bytes from {}", buffer.remaining(), sender);
                        handlePacket(sender, buffer);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.leaveGroup(MULTICAST_ADDRESS);
                    socket.close();
                } catch (IOException ignored) {}
            }
        }


        public void sendNewSolution(final Chromosome c) throws IOException {
            byte[] content = null;

            try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
                    oout.writeObject(c);
                }

                byte[] chromosome = out.toByteArray();
                content = new byte[1 + chromosome.length];

                content[0] = SOLUTION_MASK;
                System.arraycopy(chromosome, 0, content, 1, chromosome.length);
            }finally {
                if(content != null){

                    LOGGER.info(DISPATCH_MARKER, "Sending new solution {}", c);

                    socket.send(new DatagramPacket(content, content.length, MULTICAST_ADDRESS, DEFAULT_PORT));
                }
            }

        }

        private void handlePacket(SocketAddress from, ByteBuffer packet) throws IOException {
            packet.flip();

            byte header = packet.get();

            if(header == SOLUTION_MASK)
                handleSolution(from, packet);
            else
                LOGGER.error("Invalid packet header ({}) received", header);
        }

        private void handleSolution(SocketAddress from, ByteBuffer packet) throws IOException {
            byte[] content = new byte[packet.remaining()];
            try(ObjectInputStream oin = new ObjectInputStream(new ByteInputStream(content, content.length))){
                try {
                    Chromosome c = (Chromosome) oin.readObject();
                    if(Objects.nonNull(c)){
                        Chromosome fittest = fittestChromosome();
                        if(population.size() > 0 && fittest.fitness() > c.fitness()){
                            LOGGER.info(HANDLING_MARKER, "Received SOLUTION Flag, adding the chromosome to the list");
                            population.addChromosome(c);
                        }else{
                            LOGGER.info(HANDLING_MARKER, "Received SOLUTION Flag, but chromosome is lesser than fittest ({} > {})", c.fitness(), fittest.fitness());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.fatal("Chromosome Class Not Found !" , e);
                }
            }
        }
    }
}
