package asee.asee.administration.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResolvedHashResponse {
    private String url;

    private int redirectionType;
}
