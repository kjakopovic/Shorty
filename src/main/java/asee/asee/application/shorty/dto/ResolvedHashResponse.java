package asee.asee.application.shorty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResolvedHashResponse {
    private String url;

    private int redirectionType;
}
