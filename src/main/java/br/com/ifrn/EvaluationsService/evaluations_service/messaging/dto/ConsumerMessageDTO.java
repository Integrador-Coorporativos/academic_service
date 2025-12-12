package br.com.ifrn.EvaluationsService.evaluations_service.messaging.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;

@Data
public class ConsumerMessageDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull
    private String classId;
    @NotNull
    private String userId;
    private float presence;
    private float average;
    private  float ira;
    private  Integer rejections;

}
