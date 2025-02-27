package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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