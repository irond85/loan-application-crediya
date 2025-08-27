package co.irond.crediya.model.status;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Status {

    private Long id;
    private String name;
    private String description;

}
