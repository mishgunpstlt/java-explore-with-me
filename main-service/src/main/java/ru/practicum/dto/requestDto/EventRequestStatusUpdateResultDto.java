package ru.practicum.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResultDto {

    private List<RequestDto> confirmedRequests;

    private List<RequestDto> rejectedRequests;

}
