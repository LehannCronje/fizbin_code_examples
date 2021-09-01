package com.presbo.presboservice.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String url;
    private Date createdAt;
    //username of user that took the picture.
    private String createdBy;
    Long projectFileId;

    @ManyToOne
    Gallery gallery;

}
