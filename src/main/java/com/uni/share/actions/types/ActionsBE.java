package com.uni.share.actions.types;

import com.uni.share.common.types.AbstractBaseBE;

import javax.persistence.*;

@Entity
@Table(name = "t_actions")
public class ActionsBE extends AbstractBaseBE {
    @Id
    @GeneratedValue(generator = "g_t_actions", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "g_t_actions", sequenceName = "sq_actions", allocationSize = 1)
    private Long id;

    @Version
    private Long version;

    private String category;
    private String type;
    private String message;

    @Column(name = "link_to")
    private String linkTo;

    @Column(name = "user_id")
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
