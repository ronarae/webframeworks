package nl.team02.amsterdamevents.aeserver.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import nl.team02.amsterdamevents.aeserver.repositories.AEventsRepositoryMock;
import nl.team02.amsterdamevents.aeserver.views.ViewAEvent;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@NamedQueries({
        @NamedQuery(name = "AEvent_find_by_status", query = "select a from AEvent a where a.status = :status"),
        @NamedQuery(name = "AEvent_find_by_title",
               query = "select a from AEvent a where a.title like :title"
        ),
        @NamedQuery(name = "AEvent_find_by_minRegistrations", query = "select distinct a FROM AEvent a join Registration r on r.aEvent = a AND a.registrations.size >= :minRegistration")

})
@Entity(name = "AEvent")
public class AEvent {

    @Id //primary key
    @GeneratedValue
    public long id;
    @JsonView(ViewAEvent.Public.class)
    public String title;
    public LocalDate startDate;
    public LocalDate endDate;
    @JsonView(ViewAEvent.Public.class)

    public AEventStatus status;
    public double entranceFee;
    public int maxParticipants;
    public boolean isTicketed;

    @OneToMany(mappedBy = "aEvent", cascade = CascadeType.REMOVE)
    @JsonManagedReference
//    @JsonBackReference
    private List<Registration> registrations = new ArrayList<>();

    public AEvent() {
    }

    public AEvent(long id, String title, LocalDate startDate, LocalDate endDate, AEventStatus status, double entranceFee, int maxParticipants, boolean isTicketed) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.entranceFee = entranceFee;
        this.maxParticipants = maxParticipants;
        this.isTicketed = isTicketed;
    }

    public static AEvent createRandomAEvent() {
        long id = AEventsRepositoryMock.aEventId++;
        String title = "A fantastic backend aEvent-" + id;
        LocalDate start = getRandomStartDate(2020, 2021);
        LocalDate end = getRandomEndDate(start, 2021);
        AEventStatus status = getRandomAEventStatus();
        double entranceFee = getRandomEntranceFee();
        int maxParticipants = getRandomMaxParticipants();
        boolean isTicketed = getRandomIsTicketed();

        return new AEvent(id, title, start, end, status, entranceFee, maxParticipants, isTicketed);
    }

    public static LocalDate getRandomStartDate(int startYear, int endYear) {
        LocalDate startDate = LocalDate.of(startYear, 1, 1); //Min of start
        LocalDate endDate = LocalDate.of(endYear, 12, 31); //Max of end
        long start = startDate.toEpochDay();
        long end = endDate.toEpochDay();

        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
        return LocalDate.ofEpochDay(randomEpochDay);
    }

    public static LocalDate getRandomEndDate(LocalDate startDate, int endYear) {
        long start = startDate.toEpochDay();

        LocalDate endDate = LocalDate.of(endYear, 12, 31); //Max of end
        long end = endDate.toEpochDay();

        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
        return LocalDate.ofEpochDay(randomEpochDay);
    }

    public static AEventStatus getRandomAEventStatus() {
        AEventStatus[] values = AEventStatus.values();
        int length = values.length;
        int randomIndex = new Random().nextInt(length);
        return values[randomIndex];
    }

    public static double getRandomEntranceFee() {
        double minimumFee = 10.00;
        double maximumFee = 100.00;
        double generatedRandomFee = Math.floor(minimumFee + (new Random().nextDouble() * (maximumFee - minimumFee)));
        return generatedRandomFee;
    }

    public static int getRandomMaxParticipants() {
        int minimumParticipants = 1;
        int maximumParticipants = 80;
        int generatedRandomParticipants = minimumParticipants + (int) (new Random().nextFloat() * (maximumParticipants - minimumParticipants));
        return generatedRandomParticipants;
    }

    public static boolean getRandomIsTicketed() {
        Random ticketed = new Random();
        return ticketed.nextBoolean();
    }

    /**
     * @return number of registrations for this AEvent
     */
    public int getNumberOfRegistrations() {
        int count = 0;
        for (Registration registration : registrations) {
            if (this == registration.getaEvent()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Creates and add new registration for this AEvent
     * Check whethers this.status == PUBLISHED and
     * maxParticipants has not been exceeded
     //  * @param submissionDateTime
     * @return the created registration or null
     */
    public Registration createNewRegistration(LocalDateTime submissionDateTime) {
        // TODO check conditions and create and add registration
        if (this.getStatus().equals(AEventStatus.PUBLISHED) && this.getNumberOfRegistrations() < this.getMaxParticipants()){
            //create a registration
            Registration registration = new Registration();
            //set sub date
            registration.setSubmissionDate(submissionDateTime);
            //add registration
            registration.ticketCode = generateRandomCharacterString(10);
            registration.setaEvent(this);

            registrations.add(registration);

            return registration;
        }
        else {
            return null;
        }
}

    private static String generateRandomCharacterString(int amountOfCharacters) {
        final String ALPHA_NUMBERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (amountOfCharacters-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMBERIC_STRING.length());
            builder.append(ALPHA_NUMBERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void addRegistration(Registration registration) {
        this.registrations.add(registration);
        registration.setaEvent(this);
    }


    public AEventStatus getStatus() {
        return status;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setStatus(AEventStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AEvent{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", entranceFee=" + entranceFee +
                ", maxParticipants=" + maxParticipants +
                ", isTicketed=" + isTicketed +
                '}';
    }

    public enum AEventStatus {
        DRAFT,
        PUBLISHED,
        CANCELLED
    }
}




