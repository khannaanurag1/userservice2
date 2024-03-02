package org.example.userservice2.Models;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity(name="role1")
@Setter
@Getter
public class Role extends BaseModel {
    private String value;

}
