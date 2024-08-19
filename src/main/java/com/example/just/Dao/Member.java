  package com.example.just.Dao;

  import com.fasterxml.jackson.annotation.JsonIgnore;
  import java.util.Date;
  import lombok.*;
  import org.hibernate.annotations.CreationTimestamp;

  import javax.persistence.*;
  import java.sql.Timestamp;
  import java.util.ArrayList;
  import java.util.List;

  @Entity
  @Builder
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public class Member {
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      private String email;
      @Enumerated(EnumType.STRING)
      @Setter
      private Role authority;

      @CreationTimestamp
      @Column(name = "create_time")
      private Date createTime;

      private String provider;

      private String provider_id;
      private String nickname;

      //신고받은횟수
      @Column(name = "blamed_count")
      private int blamedCount;
      //신고한 횟수
      @Column(name = "blame_count")
      private int blameCount;

      @Column(name = "refresh_token")
      private String refreshToken;

      @Builder.Default //안 써도 되는데 경고떠서 그냥 부침
      @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY,  orphanRemoval=true)
      // FetchType.EAGER -> FetchType.LAZY, EntityGraph를 통해 FetchType을 정할 것이다.
      @JsonIgnore
      private List<Post> posts = new ArrayList<>();

      @Builder.Default
      @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY,  orphanRemoval=true)
      @JsonIgnore
      private List<Comment> comments = new ArrayList<>();

      @Builder.Default
      @ManyToMany(mappedBy = "likedMembers", cascade = CascadeType.REMOVE)
      private List<Post> likedPosts = new ArrayList<>();

      @Builder.Default
      @ManyToMany(mappedBy = "likedMembers", cascade = CascadeType.REMOVE)
      private List<Comment> likedComments = new ArrayList<>();
      public void addBlamed(){
          blamedCount++;
      }

      public void removeBlame() {blameCount--;}
      public void addBlame(){
          blameCount++;
      }

      @OneToMany(mappedBy = "receiver")   //알림
      private List<Notification> notifications;
      public void updateMember(final Post post) {
          posts.add(post);
      }


      public Member(Member member) {
          this.id = member.getId();
          this.email = member.getEmail();
          this.authority = member.getAuthority();
          this.createTime = member.getCreateTime();
          this.provider = member.getProvider();
          this.provider_id = member.getProvider_id();
          this.nickname = member.getNickname();
          this.blamedCount = member.getBlamedCount();
          this.blameCount = member.getBlameCount();
          this.posts = member.getPosts();
          this.likedPosts = member.getLikedPosts();
          this.notifications = member.getNotifications();
      }
  }


