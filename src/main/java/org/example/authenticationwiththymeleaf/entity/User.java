package org.example.authenticationwiththymeleaf.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    /**
     * @NaturalId thường được sử dụng trên các thuộc
     * tính có tính chất duy nhất và có thể thay thế @Id
     * trong một số trường hợp.
     * mutable = true cho phép giá trị của natural ID có
     * thể thay đổi sau khi entity đã được lưu vào cơ sở dữ liệu.
     */
    @NaturalId(mutable = true)
    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private boolean isEnabled = false;

    /**
     * @JoinTable: Xác định bảng trung gian user_roles để lưu trữ quan hệ giữa bảng user và role.
     * joinColumns: Liên kết đến bảng user thông qua cột user_id.
     * inverseJoinColumns: Liên kết đến bảng role thông qua cột role_id.
     * <p>
     * user_id:
     * Là một cột trong bảng trung gian user_roles.
     * Đây là foreign key tham chiếu đến trường id trong bảng user.
     * role_id:
     * Là cột trong bảng trung gian user_roles.
     * Đây là foreign key tham chiếu đến trường id trong bảng role.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User(String firstName, String lastName, String email, String password, Collection<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
