/*
 * ReadIRSensorsCommand.cpp
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#include "ReadUSSensorCommand.h"

namespace RobotProtocol {

ReadUSSensorCommand::ReadUSSensorCommand(N6 *robot):Command(robot) {
}

ReadUSSensorCommand::~ReadUSSensorCommand() {
	if(outgoingMessage != NULL)
		delete outgoingMessage;
}

bool ReadUSSensorCommand::isContinuous(){
	return false;
}

void ReadUSSensorCommand::reset(){
	done				= true;
}

void ReadUSSensorCommand::resetFromMessage(Message* incomingMessage){
	done				= true;
}

void ReadUSSensorCommand::execute(int updateGap){
	/*
	robot->getMessageManager()->sendMessage(*(ReusableMessageProxy::getReadUSSensorReplyMessage
			(robot->getMasterId(), robot->getRobotId()
						, robot->getUSSensorDistance())));
						*/
}

} /* namespace RobotProtocol */

