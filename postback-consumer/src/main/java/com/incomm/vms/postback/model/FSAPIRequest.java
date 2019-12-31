package com.incomm.vms.postback.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FSAPIRequest {
     private final Order order;

}
