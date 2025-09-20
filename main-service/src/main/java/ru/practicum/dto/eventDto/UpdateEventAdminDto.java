package ru.practicum.dto.eventDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.model.event.AdminActionStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminDto extends UpdateEventBaseDto {

    private AdminActionStatus stateAction;

}
