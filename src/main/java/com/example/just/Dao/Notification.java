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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "notification")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notId;

    @Column(name = "not_type")  //알림 타입
    private String notType;

    @Column(name = "not_post_id")   //알림 내용
    private Long notPostId;

    @Column(name = "not_datetime")  //알림 발생 시일
    private Date notDatetime;
//
//    @Column(name = "not_read_datetime") //알림 읽은 시간
//    private Date not_read_dateTime;

    @Column(nullable = false)
    private Boolean notIsRead; //알림 읽음 여부
    @ManyToOne
    @JoinColumn(name = "id")
    @OnDelete(action = OnDeleteAction.CASCADE) //알림을 받을 member
    private Member receiver;

    @Column(name = "not_sender_id")   //송신자 id
    private Long senderId;

    public Notification(String notType, Long notPostId, Date notDatetime, Boolean notIsRead,
                        Member receiver,
                        Long senderId) {
        this.notType = notType;
        this.notPostId = notPostId;
        this.notDatetime = notDatetime;
        this.notIsRead = notIsRead;
        this.receiver = receiver;
        this.senderId = senderId;
    }
}
