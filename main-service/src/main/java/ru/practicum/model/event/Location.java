package ru.practicum.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @NotNull
    @Column(name = "location_lat")
    private Float lat;

    @NotNull
    @Column(name = "location_lon")
    private Float lon;
}
