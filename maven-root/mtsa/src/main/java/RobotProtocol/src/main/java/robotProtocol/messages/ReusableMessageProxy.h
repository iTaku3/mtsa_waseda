/*
 * ReusableMessageProxy.h
 *
 *  Created on: Jan 20, 2014
 *      Author: marianoc
 */

#ifndef REUSABLEMESSAGEPROXY_H_
#define REUSABLEMESSAGEPROXY_H_

#include "DestinationReachedMessage.h"
#include "RobotLostMessage.h"
/*
#include "ReadUSSensorReplyMessage.h"
#include "ReadIRSensorsMessage.h"
#include "ReadIRSensorsReplyMessage.h"
#include "RobotStatusMessage.h"
*/

namespace RobotProtocol {

class ReusableMessageProxy {
public:
	ReusableMessageProxy();
	virtual ~ReusableMessageProxy();
	static DestinationReachedMessage *getDestinationReachedMessage(int to, int from);
	static RobotLostMessage *getRobotLostMessage(int to, int from);
/*
	static RobotStatusMessage *getRobotStatusMessage(int to, int from, int leftSpeedValue, int rightSpeedValue,
			int leftIRValue, int rightIRValue, int batteryValue);
	static ReadUSSensorReplyMessage *getReadUSSensorReplyMessage(int to, int from, int distance);
	static ReadIRSensorsMessage *getReadIRSensorsMessage(int to, int from);
	static ReadIRSensorsReplyMessage *getReadIRSensorsReplyMessage(int to, int from, int leftValue,
			int rightValue, int topValue);
			*/
private:
	static DestinationReachedMessage destinationReachedMessage;
	static RobotLostMessage robotLostMessage;
	/*
	static RobotStatusMessage robotStatusMessage;
	static ReadUSSensorReplyMessage readUSSensorReplyMessage;
	static ReadIRSensorsMessage readIRSensorsMessage;
	static ReadIRSensorsReplyMessage readIRSensorsReplyMessage;
	*/

};

} /* namespace RobotProtocol */
#endif /* REUSABLEMESSAGEPROXY_H_ */
