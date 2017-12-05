package com.cryptofacilities.interview;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class OrderBookManagerTest {

	// create order book
	OrderBookManager orderBookManager;

	// create order
	Order buy;
	Order buy2;

	@Before
	public void initialize() {
		orderBookManager = new OrderBookManagerImpl();

		buy = new Order("order1", "VOD.L", Side.buy, 200, 10);

		// send order
		orderBookManager.addOrder(buy);
	}

	@Test
	public void testAddOrder() {
		Order newBuyOrder = new Order("newBuyOrder", "VOD.L", Side.buy, 200, 10);

		orderBookManager.addOrder(newBuyOrder);

		long expectedOrderNum = 2;
		long actualOrderNum = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 200);
		assertEquals("Order number at price 200 for buy order is 2", expectedOrderNum, actualOrderNum);

		// test add order with new instrument
		buy2 = new Order("order2", "VOD.B", Side.buy, 300, 5);
		orderBookManager.addOrder(buy2);

		long expectedOrderNum2 = 5;
		long actualOrderNum2 = orderBookManager.getTotalQuantityAtLevel("VOD.B", Side.buy, 300);
		assertEquals("Order number at price 300 for buy order is 5", expectedOrderNum2, actualOrderNum2);
	}

	@Test
	public void testModifyOrder() {
		Order newBuyOrder = new Order("newBuyOrder", "VOD.L", Side.buy, 200, 50);

		orderBookManager.addOrder(newBuyOrder);

		// test increased quantity
		orderBookManager.modifyOrder("order1", 33);

		long expectedIncreasedQty = 83;
		long actualIncreasedQty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 200);
		assertEquals("Order quantity at price 200 for buy order is 83", expectedIncreasedQty, actualIncreasedQty);

		// test decreased quantity
		orderBookManager.modifyOrder("order1", 2);

		long expectedDecreasedQty = 52;
		long actualDecreasedQty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 200);
		assertEquals("Order quantity at price 200 for buy order is 52", expectedDecreasedQty, actualDecreasedQty);
	}

	@Test
	public void testDeleteOrder() {
		orderBookManager.deleteOrder("order1");

		long expectedOrderNum = 0;
		long actualOrderNum = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 200);
		assertEquals("Order number at price 200 for buy order is 0", expectedOrderNum, actualOrderNum);
	}

	@Test
	public void testBestBidPrice() {
		// check that best price is 200
		long expectedPrice = 200;
		long actualPrice = orderBookManager.getBestPrice("VOD.L", Side.buy);
		assertEquals("Best bid price is 200", expectedPrice, actualPrice);
	}

	@Test
	public void testGetOrderNumAtLevel() {
		long expectedOrderNum = 1;
		long actualOrderNum = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 200);
		assertEquals("Order number at price 200 for buy order is 1", expectedOrderNum, actualOrderNum);
	}

	@Test
	public void testGetTotalQuantityAtLevel() {
		long expectedTotalQty = 10;
		long actualTotalQty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 200);
		assertEquals("Total quantity at price 200 for buy order is 10", expectedTotalQty, actualTotalQty);
	}

	@Test
	public void testGetTotalVolumeAtLevel() {
		Order newBuyOrder1 = new Order("newBuyOrder1", "ABC", Side.buy, 200, 10);
		Order newBuyOrder2 = new Order("newBuyOrder2", "ABC", Side.buy, 200, 10);
		Order newBuyOrder3 = new Order("newBuyOrder3", "ABC", Side.buy, 200, 10);
		// this order is from different instrument, to test that it is not counted
		Order newBuyOrder4 = new Order("newBuyOrder4", "DEF", Side.buy, 200, 10);

		orderBookManager.addOrder(newBuyOrder1);
		orderBookManager.addOrder(newBuyOrder2);
		orderBookManager.addOrder(newBuyOrder3);
		orderBookManager.addOrder(newBuyOrder4);

		long expectedTotalVol = 6000;
		long actualTotalVol = orderBookManager.getTotalVolumeAtLevel("ABC", Side.buy, 200);
		assertEquals("Total volume at price 200 for buy order of ABC is 6000", expectedTotalVol, actualTotalVol);
	}

	@Test
	public void testGetOrdersAtLevel() {
		Order newBuyOrder1 = new Order("newBuyOrder1", "VOD.L", Side.buy, 200, 10);
		orderBookManager.addOrder(newBuyOrder1);

		Order[] expectedOrders = { buy, newBuyOrder1 };
		assertArrayEquals(expectedOrders, orderBookManager.getOrdersAtLevel("VOD.L", Side.buy, 200).toArray());

		assertTrue("Orders on instrument XXX is empty",
				orderBookManager.getOrdersAtLevel("XXX", Side.sell, 200).isEmpty());
	}
}
