package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
@Entity
@Getter
@Setter
@Table(
        name = "HashTag",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_name", columnNames = "name")
        }
)
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hash_tag_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "tag_count")
    private Long tagCount;

    @OneToMany(mappedBy = "hashTag", fetch = FetchType.LAZY)
    private List<HashTagMap> hashTagMaps = new ArrayList<>();


    public HashTag() {
    }

    public HashTag(String name) {
        this.name = name;
        this.tagCount = 0L;
    }

    public HashTag(Long hashTagId, String name, Long tagCount) {
        this.id= hashTagId;
        this.name = name;
        this.tagCount = tagCount;
    }

    public void addHashTagMap(HashTagMap hashTagMap) {
        this.hashTagMaps.add(hashTagMap);
    }
}