package it.aeg2000srl.agent.core;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by tiziano.michelessi on 09/10/2015.
 */
public class TOrder extends SugarRecord<TOrder> {
    public Date creationDate;
    public String notes;
    public Date sentDate;
    public int type;        //0: normale, 1: icewer

    // relationships
    public TUser TUser;
    public TCustomer customer;
}
