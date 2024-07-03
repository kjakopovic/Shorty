package asee.shortyapplication.shorty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResolvedHashResponse {
    private String url;

    private int redirectionType;
}
