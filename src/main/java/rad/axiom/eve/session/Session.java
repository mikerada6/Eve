package rad.axiom.eve.session;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Session {

    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    @ManyToOne
    @JoinColumn(name="walle", nullable=false)
    private Walle walle;
    private long startTime;
    private long endTime;
    private long lastUpdated;
    private SessionStatus status;

}
