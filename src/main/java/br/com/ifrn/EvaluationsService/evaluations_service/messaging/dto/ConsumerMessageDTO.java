package br.com.ifrn.EvaluationsService.evaluations_service.messaging.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ConsumerMessageDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private String classId;
    private String userId;
    private float presence;
    private float average;
    private  float ira;
    private  Integer rejections;

}
