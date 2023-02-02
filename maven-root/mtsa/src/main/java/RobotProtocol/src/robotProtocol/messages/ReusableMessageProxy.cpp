/*
 * ReusableMessageProxy.cpp
 *
 *  Created on: Jan 20, 2014
 *      Author: marianoc
 */

#include "ReusableMessageProxy.h"

namespace RobotProtocol {

DestinationReachedMessage ReusableMessageProxy::destinationReachedMessage 	= DestinationReachedMessage(0,0);
RobotLostMessage ReusableMessageProxy::robotLostMessage						= RobotLostMessage(0,0);
/*
RobotStatusMessage ReusableMessageProxy::robotStatusMessage 				= RobotStatusMessage(0,0,0,0,0,0,0);
ReadUSSensorReplyMessage ReusableMessageProxy::readUSSensorReplyMessage		= ReadUSSensorReplyMessage(0,0,0);
ReadIRSensorsMessage ReusableMessageProxy::readIRSensorsMessage				= ReadIRSensorsMessage(0,0);
ReadIRSensorsReplyMessage ReusableMessageProxy::readIRSensorsReplyMessage	= ReadIRSensorsReplyMessage(0,0,0,0,0);
*/

ReusableMessageProxy::ReusableMessageProxy() {
	// TODO Auto-generated constructor stub

}

ReusableMessageProxy::~ReusableMessageProxy() {
	// TODO Auto-generated destructor stub
}

DestinationReachedMessage* ReusableMessageProxy::getDestinationReachedMessage(int to, int from){
	destinationReachedMessage.setToAddress(to);
	destinationReachedMessage.setFromAddress(from);
	return &destinationReachedMessage;
}

RobotLostMessage* ReusableMessageProxy::getRobotLostMessage(int to, int from){
	robotLostMessage.setToAddress(to);
	robotLostMessage.setFromAddress(from);
	return &robotLostMessage;
}

/*
RobotStatusMessage* ReusableMessageProxy::getRobotStatusMessage(int to, int from, int leftSpeedValue, int rightSpeedValue,
		int leftIRValue, int rightIRValue, int batteryValue){
	robotStatusMessage.setToAddress(to);
	robotStatusMessage.setFromAddress(from);
	robotStatusMessage.setLeftSpeedValue(leftSpeedValue);
	robotStatusMessage.setRightSpeedValue(rightSpeedValue);
	robotStatusMessage.setLeftIRValue(leftIRValue);
	robotStatusMessage.setRightIRValue(rightIRValue);
	robotStatusMessage.setBatteryValue(batteryValue);
	return &robotStatusMessage;
}

ReadUSSensorReplyMessage* ReusableMessageProxy::getReadUSSensorReplyMessage(int to, int from, int distance){
	readUSSensorReplyMessage.setToAddress(to);
	readUSSensorReplyMessage.setFromAddress(from);
	readUSSensorReplyMessage.setDistance(distance);
	return &readUSSensorReplyMessage;
}

ReadIRSensorsMessage* ReusableMessageProxy::getReadIRSensorsMessage(int to, int from){
	readIRSensorsMessage.setToAddress(to);
	readIRSensorsMessage.setFromAddress(from);
	return &readIRSensorsMessage;
}

ReadIRSensorsReplyMessage* ReusableMessageProxy::getReadIRSensorsReplyMessage(int to, int from,
		int leftValue, int rightValue, int topValue){
	readIRSensorsReplyMessage.setToAddress(to);
	readIRSensorsReplyMessage.setFromAddress(from);
	readIRSensorsReplyMessage.setLeftValue(leftValue);
	readIRSensorsReplyMessage.setRightValue(rightValue);
	readIRSensorsReplyMessage.setTopValue(topValue);
	return &readIRSensorsReplyMessage;
}
*/

} /* namespace RobotProtocol */
