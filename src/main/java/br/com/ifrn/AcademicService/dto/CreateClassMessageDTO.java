package br.com.ifrn.AcademicService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateClassMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseName;
    private String semester;
    private String gradle_level;
    private String shift;
    private String userId;
    private String classId;
}
