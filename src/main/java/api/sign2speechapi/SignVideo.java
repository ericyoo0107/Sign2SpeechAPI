package api.sign2speechapi;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "sign_video")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class SignVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "gloss", nullable = false)
    private String gloss;

    @Column(name = "sign_video_url", nullable = false)
    private String videoUrl;

    @Column(name = "sign_description", nullable = true, length = 1000)
    private String description;

    @Column(name = "sign_images", nullable = true, length = 1000)
    private String imgUrl;
}
