package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
<<<<<<< HEAD
=======
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
<<<<<<< HEAD
import javax.persistence.ManyToOne;
=======
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Builder.Default;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
<<<<<<< HEAD
@NoArgsConstructor
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
=======
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hash_tag_id")
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    private Long id;

    @Column(name = "name")
    private String name;
<<<<<<< HEAD
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "post_id")
    private Post post;
=======

    @Column(name = "tag_count")
    private Long tagCount;

    @OneToMany(mappedBy = "hashTag", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<HashTagMap> hashTagMaps = new ArrayList<>();


    public HashTag() {
    }
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828

    public HashTag(String name) {
        this.name = name;
    }
<<<<<<< HEAD
=======

    public void addHashTagMap(HashTagMap hashTagMap) {
        this.hashTagMaps.add(hashTagMap);
    }
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
}