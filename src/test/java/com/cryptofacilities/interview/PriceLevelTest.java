package com.cryptofacilities.interview;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

public class PriceLevelTest {

	PriceLevel priceLevel;
	String instrument = "VOD.L";
	Side side = Side.buy;
	long price = 333;
	Order order1;
	Order order2;
	Order order3;

	@Before
	public void initialize() {
		LinkedList<Order> orders = new LinkedList<Order>();
		order1 = new Order("buy1", instrument, side, price, 10);
		order2 = new Order("buy2", instrument, side, price, 5);
		order3 = new Order("buy3", instrument, side, price, 80);

		orders.add(order1);
		orders.add(order2);
		orders.add(order3);

		priceLevel = new PriceLevel(orders, instrument, side, price);
	}

	@Test
	public void testAddOrder() {
		Order order4 = new Order("buy4", instrument, side, price, 2);
		priceLevel.addOrder(order4);

		int expectedSize = 4;
		int actualSize = priceLevel.getOrders().size();
		assertEquals("Size is 4", expectedSize, actualSize);

		Order expectedOrder = new Order(order4);
		Order actualOrder = priceLevel.getOrders().get(3);
		assertEquals("4th order is order4", expectedOrder, actualOrder);
	}

	@Test
	public void testModifyOrder() {
		priceLevel.modifyOrder("buy2", 100);

		long expectedQty = 100;
		long actualQty = priceLevel.getOrderById("buy2").getQuantity();
		assertEquals("New quantity is 100", expectedQty, actualQty);

		String expectedLastOrderId = "buy2";
		String actualLastOrderId = priceLevel.getOrders().getLast().getOrderId();
		assertEquals("Last order is buy2", expectedLastOrderId, actualLastOrderId);
	}

	@Test
	public void testDeleteOrder() {
		priceLevel.deleteOrder(order3.getOrderId());

		int expectedSize = 2;
		int actualSize = priceLevel.getOrders().size();
		assertEquals("Size is 2", expectedSize, actualSize);

		Order expectedOrder = null;
		Order actualOrder = priceLevel.getOrderById(order3.getOrderId());
		assertEquals("order3 is deleted and cannot be found", expectedOrder, actualOrder);
	}

	@Test
	public void testGetOrderNum() {
		long expectedSize = 3;
		long actualSize = priceLevel.getOrderNum();
		assertEquals("Size is 3", expectedSize, actualSize);
	}

	@Test
	public void testGetTotalQuantity() {
		long expectedQty = 95;
		long actualQty = priceLevel.getTotalQuantity();
		assertEquals("Total quantity is 95", expectedQty, actualQty);
	}

	@Test
	public void testGetTotalVolume() {
		long expectedVolume = 31635;
		long actualVolume = priceLevel.getTotalVolume();
		assertEquals("Total volume is 31635", expectedVolume, actualVolume);
	}

	@Test
	public void testGetOrders() {
		Object[] orderList = { order1, order2, order3 };
		assertArrayEquals("Price Level is eqaul to orders of 1, 2 & 3", orderList, priceLevel.getOrders().toArray());
	}

	@Test
	public void testGetOrderById() {
		Order expectedOrder = order2;
		Order actualOrder = priceLevel.getOrderById("buy2");
		assertEquals("Order is buy2", expectedOrder, actualOrder);
	}
}
