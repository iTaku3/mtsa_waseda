/*
 * ReadIRSensorsCommand.h
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_READUSSENSORCOMMAND_H_
#define ROBOTPROTOCOL_READUSSENSORCOMMAND_H_

#include "../Command.h"
#include "../messages/ReadUSSensorMessage.h"
#include "../messages/ReadUSSensorReplyMessage.h"
#include "../messages/ReusableMessageProxy.h"

namespace RobotProtocol {

class ReadUSSensorCommand: public RobotProtocol::Command {
public:
	ReadUSSensorCommand(N6 *robot);
	virtual ~ReadUSSensorCommand();
	virtual void reset();
	virtual void resetFromMessage(Message* message);
	virtual void execute(int updateGap);
	virtual bool isContinuous();
private:
	ReadUSSensorReplyMessage *outgoingMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_READUSSENSORCOMMAND_H_ */

