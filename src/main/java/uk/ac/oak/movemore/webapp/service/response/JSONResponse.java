package uk.ac.oak.movemore.webapp.service.response;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value="JSON response for sensor web system")
public class JSONResponse implements Serializable{

	private static final long serialVersionUID = -4509200296057923020L;
	
	
	private Integer isSuccess;
	
	public Integer getIsSuccess() {
		return isSuccess;
	}
	
	@ApiModelProperty(value="Success Status", required=true, allowableValues="-2,-1,0,1")
	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	
}
