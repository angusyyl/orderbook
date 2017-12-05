package com.cryptofacilities.interview;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by CF-8 on 6/27/2017.
 */
public class OrderBookManagerImpl implements OrderBookManager {

	// Map of instrument to OrderBook
	private final Map<String, OrderBook> orderBooksMap = new HashMap<String, OrderBook>();

	// Map of orderId to Order
	private final Map<String, Order> ordersMap = new HashMap<String, Order>();

	public void addOrder(Order order) {
		String instrument = order.getInstrument();
		OrderBook orderBook = orderBooksMap.get(instrument);
		if (null == orderBook) {
			orderBook = new OrderBook(instrument);
			orderBooksMap.put(instrument, orderBook);
		}

		orderBook.addOrder(order);
		ordersMap.put(order.getOrderId(), order);
	}

	public void modifyOrder(String orderId, long newQuantity) {
		Order order = ordersMap.get(orderId);
		if (null != order) {
			String instrument = order.getInstrument();
			OrderBook orderBook = orderBooksMap.get(instrument);
			orderBook.modifyOrder(orderId, newQuantity);
		} else {
			System.out.printf("No such order with Id %s is found to modify.\n", orderId);
		}
	}

	public void deleteOrder(String orderId) {
		Order order = ordersMap.get(orderId);
		if (null != order) {
			String instrument = order.getInstrument();
			OrderBook orderBook = orderBooksMap.get(instrument);
			orderBook.deleteOrder(orderId);
			ordersMap.remove(orderId);
		} else {
			System.out.printf("No such order with Id %s is found to delete.\n", orderId);
		}
	}

	public long getBestPrice(String instrument, Side side) {
		OrderBook orderBook = orderBooksMap.get(instrument);
		return orderBook == null ? 0 : orderBook.getBestPrice(side);
	}

	public long getOrderNumAtLevel(String instrument, Side side, long price) {
		OrderBook orderBook = orderBooksMap.get(instrument);
		return orderBook == null ? 0 : orderBook.getOrderNumAtLevel(side, price);
	}

	public long getTotalQuantityAtLevel(String instrument, Side side, long price) {
		OrderBook orderBook = orderBooksMap.get(instrument);
		return orderBook == null ? 0 : orderBook.getTotalQuantityAtLevel(side, price);
	}

	public long getTotalVolumeAtLevel(String instrument, Side side, long price) {
		OrderBook orderBook = orderBooksMap.get(instrument);
		return orderBook == null ? 0 : orderBook.getTotalVolumeAtLevel(side, price);
	}

	public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
		OrderBook orderBook = orderBooksMap.get(instrument);
		return orderBook == null ? new LinkedList<Order>() : orderBook.getOrdersAtLevel(side, price);
	}
}
