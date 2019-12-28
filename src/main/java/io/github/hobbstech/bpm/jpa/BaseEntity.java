package io.github.hobbstech.bpm.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
@Data
public class BaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @CreatedBy
    @Column
    private String createdBy;

    @LastModifiedBy
    @Column
    private String lastModifiedBy;

    @UpdateTimestamp
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "en_ZW", timezone = "Africa/Harare")
    private Date lastModifiedDate;

    @CreationTimestamp
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "en_ZW", timezone = "Africa/Harare")
    private Date createdDate;

    @Version
    private Integer version;

    @Column
    private boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
