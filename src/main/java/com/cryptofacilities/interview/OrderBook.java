package com.cryptofacilities.interview;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook {
	private final TreeMap<Long, PriceLevel> sellPriceLevels = new TreeMap<Long, PriceLevel>();

	private final TreeMap<Long, PriceLevel> buyPriceLevels = new TreeMap<Long, PriceLevel>();

	private final String instrument;

	// Map of orderId to Order
	Map<String, Order> orderMap = new HashMap<String, Order>();

	// Constructor
	public OrderBook(String instrument) {
		super();
		this.instrument = instrument;
	}

	/**
	 * Add a placeholder for group of orders with same price and side
	 * 
	 * @param price
	 * @param side
	 */
	public void addPriceLevel(long price, Side side) {
		if (side.equals(Side.buy)) {
			buyPriceLevels.put(price, new PriceLevel(new LinkedList<Order>(), instrument, side, price));
		} else if (side.equals(Side.sell)) {
			sellPriceLevels.put(price, new PriceLevel(new LinkedList<Order>(), instrument, side, price));
		}
	}

	/**
	 * Delete the placeholder for group of orders with same price and side
	 * 
	 * @param price
	 * @param side
	 */
	public void deletePriceLevel(long price, Side side) {
		if (side.equals(Side.buy)) {
			getBuyPriceLevels().remove(price);
		} else if (side.equals(Side.sell)) {
			getSellPriceLevels().remove(price);
		}
	}

	/**
	 * Get group of orders at a price level on a side
	 * 
	 * @param price
	 * @param side
	 * @return
	 */
	public PriceLevel getPriceLevel(long price, Side side) {
		if (side.equals(Side.buy)) {
			return buyPriceLevels.get(price);
		} else if (side.equals(Side.sell)) {
			return sellPriceLevels.get(price);
		} else {
			return null;
		}
	}

	/**
	 * Add new order. New order specifies instrument, side (either buy or sell),
	 * requested quantity and price
	 * 
	 * @param order
	 */
	public void addOrder(Order order) {
		Side side = order.getSide();
		long price = order.getPrice();

		PriceLevel priceLv = getPriceLevel(price, side);
		if (null == priceLv) {
			// create new price level group
			addPriceLevel(price, side);
			priceLv = getPriceLevel(price, side);
		}
		// append new order to last of the queue
		priceLv.addOrder(order);
		orderMap.put(order.getOrderId(), order);
	}

	/**
	 * Modify existing order. Only modification of an order's quantity is allowed.
	 * You need to specify the unique order id of an existing order and the new
	 * quantity. If the quantity is decreased, the order maintains its position in
	 * the queue of orders for the relevant price level. If the quantity is
	 * increased, the order is put at the end of the queue of orders for the
	 * relevant price level.
	 * 
	 * @param orderId
	 * @param newQuantity
	 */
	public void modifyOrder(String orderId, long newQuantity) {
		Order order = orderMap.get(orderId);

		if (null != order) {
			Side side = order.getSide();
			long price = order.getPrice();

			PriceLevel priceLv = getPriceLevel(price, side);
			if (null != priceLv) {
				priceLv.modifyOrder(orderId, newQuantity);
			} else {
				System.out.printf("No such price level at %d on side %s is found to modify.\n", price, side);
			}
		} else {
			System.out.printf("No such order with Id %s is found to modify.\n", orderId);
		}
	}

	/**
	 * Delete existing order. Need to specify the unique order id of an existing
	 * order. The order is removed from the order book completely.
	 * 
	 * @param orderId
	 */
	public void deleteOrder(String orderId) {
		Order order = orderMap.get(orderId);

		if (null != order) {
			Side side = order.getSide();
			long price = order.getPrice();

			PriceLevel priceLv = getPriceLevel(price, side);
			if (null != priceLv) {
				priceLv.deleteOrder(order.getOrderId());
				orderMap.remove(order.getOrderId(), order);

				// remove the price level if it becomes empty after order deletion
				if (priceLv.getOrderNum() == 0) {
					deletePriceLevel(price, side);
				}
			} else {
				System.out.printf("No such price level at %d on side %s is found to delete.\n", price, side);
			}
		} else {
			System.out.printf("No such order with Id %s is found to delete.\n", orderId);
		}
	}

	/**
	 * Get best bid or ask price
	 * 
	 * @param side
	 * @return
	 */
	public long getBestPrice(Side side) {
		if ((side.equals(Side.buy) && !buyPriceLevels.isEmpty())
				|| (side.equals(Side.sell) && !sellPriceLevels.isEmpty())) {
			if (side.equals(Side.buy)) {
				Long price = buyPriceLevels.lastKey();
				if (null != price) {
					return price.longValue();
				}
			} else if (side.equals(Side.sell)) {
				Long price = sellPriceLevels.lastKey();
				if (null != price) {
					return price.longValue();
				}
			}
		}
		return 0;
	}

	/**
	 * Get number of orders at a level
	 * 
	 * @return
	 */
	public long getOrderNumAtLevel(Side side, long price) {
		if ((side.equals(Side.buy) && !buyPriceLevels.isEmpty())
				|| (side.equals(Side.sell) && !sellPriceLevels.isEmpty())) {
			if (side.equals(Side.buy)) {
				PriceLevel priceLv = buyPriceLevels.get(price);
				if (null != priceLv) {
					return priceLv.getOrderNum();
				}
			} else if (side.equals(Side.sell)) {
				PriceLevel priceLv = sellPriceLevels.get(price);
				if (null != priceLv) {
					return priceLv.getOrderNum();
				}
			}
		}
		return 0;
	}

	/**
	 * Get total tradeable quantity at a price level
	 * 
	 * @return
	 */
	public long getTotalQuantityAtLevel(Side side, long price) {
		if ((side.equals(Side.buy) && !buyPriceLevels.isEmpty())
				|| (side.equals(Side.sell) && !sellPriceLevels.isEmpty())) {
			if (side.equals(Side.buy)) {
				PriceLevel priceLv = buyPriceLevels.get(price);
				if (null != priceLv) {
					return priceLv.getTotalQuantity();
				}
			} else if (side.equals(Side.sell)) {
				PriceLevel priceLv = sellPriceLevels.get(price);
				if (null != priceLv) {
					return priceLv.getTotalQuantity();
				}
			}
		}
		return 0;
	}

	/**
	 * Get total tradeable volume at a price level, i.e. sum of price*quantity for
	 * all orders
	 * 
	 * @return
	 */
	public long getTotalVolumeAtLevel(Side side, long price) {
		if (side.equals(Side.buy)) {
			PriceLevel priceLv = buyPriceLevels.get(price);
			if (null != priceLv) {
				return priceLv.getTotalVolume();
			}
		} else if (side.equals(Side.sell)) {
			PriceLevel priceLv = sellPriceLevels.get(price);
			if (null != priceLv) {
				return priceLv.getTotalVolume();
			}
		}
		return 0;
	}

	/**
	 * Get list of orders at a price level, in the correct order
	 * 
	 * @return
	 */
	public LinkedList<Order> getOrdersAtLevel(Side side, long price) {
		if (side.equals(Side.buy)) {
			PriceLevel priceLv = getBuyPriceLevels().get(price);
			if (null != priceLv) {
				return priceLv.getOrders();
			}
		} else if (side.equals(Side.sell)) {
			PriceLevel priceLv = getSellPriceLevels().get(price);
			if (null != priceLv) {
				return priceLv.getOrders();
			}
		}
		return new LinkedList<Order>();
	}

	public TreeMap<Long, PriceLevel> getSellPriceLevels() {
		return sellPriceLevels;
	}

	public TreeMap<Long, PriceLevel> getBuyPriceLevels() {
		return buyPriceLevels;
	}

	public String getInstrument() {
		return instrument;
	}
}
