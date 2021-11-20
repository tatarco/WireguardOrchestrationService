package com.talon.sec.wireguardorchestrationservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class IpNotFoundException extends RuntimeException
{
    public IpNotFoundException(String msg)
    {
        super("IpNotFoundException: "+msg);
    }
}
