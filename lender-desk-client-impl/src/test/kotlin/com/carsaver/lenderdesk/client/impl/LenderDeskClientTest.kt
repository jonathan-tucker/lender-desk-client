package com.carsaver.lenderdesk.client.impl

import com.carsaver.lender.desk.client.impl.LenderDeskClientImpl
import com.carsaver.lender.desk.client.impl.TransactionType
import com.carsaver.lender.desk.client.impl.VehicleStatus

class LenderDeskClientTest {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val appId = "autodata-npJV2ZmvdOp3aMtBwrMORxebSv42hqfvoClX2XYw"
            val sharedSecret = "34cfd765bd6cfa23d0e2c0e4170913564c0ea514d757544793dcff8c4e499889"

            val client = LenderDeskClientImpl(appId, sharedSecret)
            val result = client.fetchEnhancedIncentivesOffers(
                "WBX73EF06P5V99044", "33444", creditScore = 790,
                lenders = listOf("US-88"), previousOwnershipStyleID = 414641, terms = listOf(12,36,48),
                transType = TransactionType.Lease, vehicleStatus = listOf(VehicleStatus.New)
            )

            println(result)
        }
    }
}