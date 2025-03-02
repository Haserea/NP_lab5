package ua.edu.chmnu.net_dev.c4.tcp.user.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLogin implements Serializable {
    private String username;

    private String password;
}
