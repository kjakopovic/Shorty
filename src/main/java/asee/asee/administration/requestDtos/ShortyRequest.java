package asee.asee.administration.requestDtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortyRequest {
    private String url;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Integer redirectType = 302;
}
