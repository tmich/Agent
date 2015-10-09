package it.aeg2000srl.agent.core;

import com.orm.SugarRecord;

/**
 * Created by tiziano.michelessi on 09/10/2015.
 */
public class TOrderItem extends SugarRecord<TOrderItem> {
    public int quantity;
    public String discount;
    public String notes;
    public String productCode;
    public String productName;
    public String productId;

    public TOrder eTOrder;
}
