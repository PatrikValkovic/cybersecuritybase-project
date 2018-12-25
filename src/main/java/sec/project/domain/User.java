package sec.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Getter
    @Setter
    private long Id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

}
