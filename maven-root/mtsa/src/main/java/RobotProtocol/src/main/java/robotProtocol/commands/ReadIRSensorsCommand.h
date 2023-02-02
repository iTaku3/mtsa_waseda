/*
 * ReadIRSensorsCommand.h
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_READIRSENSORSCOMMAND_H_
#define ROBOTPROTOCOL_READIRSENSORSCOMMAND_H_

#include "../Command.h"
#include "../messages/ReadIRSensorsMessage.h"
#include "../messages/ReadIRSensorsReplyMessage.h"
#include "../messages/ReusableMessageProxy.h"

namespace RobotProtocol {

class ReadIRSensorsCommand: public RobotProtocol::Command {
public:
	ReadIRSensorsCommand(N6 *robot);
	virtual ~ReadIRSensorsCommand();
	virtual void reset();
	virtual void resetFromMessage(Message* message);
	virtual void execute(int updateGap);
	virtual bool isContinuous();
private:
	ReadIRSensorsReplyMessage *outgoingMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_READIRSENSORSCOMMAND_H_ */

