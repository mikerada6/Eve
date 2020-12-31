package rad.axiom.eve.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Walle {

    @Id
    private String id;
    private String address;
    @JsonIgnore
    @OneToMany(mappedBy="walle")
    private Set<Session> sessions;

}
