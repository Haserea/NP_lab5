package ua.edu.chmnu.net_dev.c4.tcp.user.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authorization implements Serializable {
    private String token;
}
