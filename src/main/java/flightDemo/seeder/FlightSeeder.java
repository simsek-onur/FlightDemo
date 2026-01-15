package flightDemo.seeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import flightDemo.event.FlightMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class FlightSeeder {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Random RND = new Random();

    private static final List<String> IATACodes = List.of(
            "IST","SAW","ESB","ADB","AYT","DLM","BJV","TZX","GZP","ASR",
            "LHR","LGW","STN","MAN","BHX","EDI","GLA","DUB","CDG","ORY","AMS",
            "FRA","MUC","BER","MAD","BCN","VLC","LIS","OPO","MXP","FCO","VCE",
            "NAP","ATH","SKG","ZRH","GVA","VIE","PRG","WAW","BUD","OTP","SOF",
            "BEG","CPH","OSL","ARN","HEL","JFK","EWR","BOS","IAD","YYZ","DXB",
            "DOH","AUH","RUH","JED","CAI","TLV"
    );

    public static void main(String[] args) {
        var bootstrap = env("BOOTSTRAP_SERVERS", "localhost:9092");
        var topic = env("TOPIC", "flight-context");
        var count = Integer.parseInt(env("COUNT", "500"));

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "10");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            java.util.stream.IntStream.range(0, count).forEach(i -> {
                try {
                    FlightMessage message = makeMessage(i);

                    String key = message.getFlightId();
                    String json = MAPPER.writeValueAsString(message);

                    producer.send(new ProducerRecord<>(topic, key, json));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to seed message at index " + i, e);
                }
            });

            producer.flush();
        }

    }

    private static FlightMessage makeMessage(Integer i) {
        var carrierCode = "OA";
        var flightDate = LocalDate.of(2026, 1, 15).plusDays(i / 50);
        var flightNumber = 100 + RND.nextInt(1900);
        var departureAirport = pickAirport();
        var arrivalAirport = pickDifferentAirport(departureAirport);

        return FlightMessage.builder()
                .flightId(String.format("%s-%s-%s",
                        carrierCode,
                        flightDate,
                        UUID.randomUUID().toString().substring(0, 8)))
                .carrierCode(carrierCode)
                .flightNumber((long) flightNumber)
                .flightDate(flightDate)
                .traffic(RND.nextBoolean() ? "DOMESTIC" : "INTERNATIONAL")
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .build();
    }

    private static String pickAirport() {
        return IATACodes.get(RND.nextInt(IATACodes.size()));
    }

    private static String pickDifferentAirport(String notThis) {
        String a;
        do {
            a = pickAirport();
        } while (a.equals(notThis));
        return a;
    }

    private static String env(String name, String def) {
        String v = System.getenv(name);
        return (Objects.isNull(v) || v.isBlank()) ? def : v;
    }
}
