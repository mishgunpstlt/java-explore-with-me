package ru.practicum.model.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false, columnDefinition = "VARCHAR(2000)")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Category category;

    private LocalDateTime createdOn;

    @Column(name = "description", nullable = false, columnDefinition = "VARCHAR(7000)")
    private String description;

    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User initiator;

    @Embedded
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventStatus state;

    private String title;
}
