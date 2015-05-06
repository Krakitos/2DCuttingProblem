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
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Enumeration;
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
        population.addChromosome(chromosome);

        try {
            //Check by reference, if the fittest chromosome is $chromosome,
            //that means we need to dispatch a new best solution
            if (population.fittestChromosome() == chromosome){
                server.sendNewSolution(chromosome);
            }
        } catch (IOException e) {
            LOGGER.warn("Unable to send the new chromosome", e);
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

        private static final int BUFFER_SIZE = 1024;

        private static final byte SOLUTION_MASK = 1;
        private static final byte HELLO_MASK = 2;

        private MulticastSocket socket;
        private InetAddress me;

        public DistributedServer(int port) throws IOException {
            socket = new MulticastSocket(port);
            socket.joinGroup(MULTICAST_ADDRESS);
        }

        @Override
        public void run() {
            try {
                sendHello();

                for(;;) {
                    DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    socket.receive(packet);

                    if (packet.getLength() > 0 && !isMe(packet.getAddress())) {
                        if(LOGGER.isDebugEnabled())
                            LOGGER.debug(HANDLING_MARKER, "Received packet with {} bytes from {}", packet.getLength(), packet.getAddress());

                        handlePacket(packet);
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

        private boolean isMe(InetAddress sender) throws SocketException {
            if(Objects.isNull(me)) {
                //Check if the hello doesn't come from here
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface i = interfaces.nextElement();

                    Enumeration<InetAddress> addresses = i.getInetAddresses();

                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();

                        if (address.equals(sender)) {
                            me = address;
                        }
                    }
                }
            }

            return me.equals(sender);
        }

        private void sendHello() throws IOException {
            socket.send(new DatagramPacket(new byte[]{HELLO_MASK}, 1, MULTICAST_ADDRESS, DEFAULT_PORT));
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

        private void handlePacket(DatagramPacket packet) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());
            byte header = buffer.get();

            if(header == SOLUTION_MASK)
                handleSolution(packet.getAddress(), packet);
            else if(header == HELLO_MASK)
                handleHello(packet.getAddress(), packet);
            else
                LOGGER.error("Invalid packet header ({}) received", header);
        }

        /**
         * Hello handling. The method will send the best chromosome find until now
         * @param sender Sender of the packet
         * @param packet Content of the message
         * @throws IOException
         */
        private void handleHello(InetAddress sender, DatagramPacket packet) throws IOException {
            LOGGER.info(HANDLING_MARKER, "Received HELLO Flag");

            if(population.size() > 0)
                sendNewSolution(fittestChromosome());
        }


        /**
         * Solution handling. This method will add the chromosome to the population if
         * <ul>
         *     <li>Chromosome is not null</li>
         *     <li>Chromosome class is known of this JVM</li>
         *     <li>Chromosome is same length of population's chromosomes</li>
         * </ul>
         * @param sender Sender of the packet
         * @param packet Content of the message
         * @throws IOException
         */
        private void handleSolution(InetAddress sender, DatagramPacket packet) throws IOException {
            byte[] content = packet.getData();
            try(ObjectInputStream oin = new ObjectInputStream(new ByteInputStream(content, 1, content.length))){
                try {
                    Chromosome c = (Chromosome) oin.readObject();
                    if(Objects.nonNull(c) && c.length() == fittestChromosome().length()){
                        LOGGER.info(HANDLING_MARKER, "Received SOLUTION Flag, adding the chromosome {{} -> {}} to the list", c, c.fitness());
                        population.addChromosome(c);
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.fatal("Chromosome Class Not Found !" , e);
                }
            }
        }
    }
}
