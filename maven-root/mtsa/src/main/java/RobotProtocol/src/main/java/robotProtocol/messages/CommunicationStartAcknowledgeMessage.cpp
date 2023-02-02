/*
 * CommunicationStartAcknowledgeMessage.cpp
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 */

#include "CommunicationStartAcknowledgeMessage.h"

namespace RobotProtocol {

CommunicationStartAcknowledgeMessage CommunicationStartAcknowledgeMessage::staticMessage = CommunicationStartAcknowledgeMessage(0,0);

Message *CommunicationStartAcknowledgeMessage::deserialize(char *serializedMessage){
	CommunicationStartAcknowledgeMessage::staticMessage.initialize(serializedMessage);
	return &(CommunicationStartAcknowledgeMessage::staticMessage);
}

CommunicationStartAcknowledgeMessage::CommunicationStartAcknowledgeMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

CommunicationStartAcknowledgeMessage::CommunicationStartAcknowledgeMessage(char *serializedMessage){
	initialize(serializedMessage);
}

CommunicationStartAcknowledgeMessage::~CommunicationStartAcknowledgeMessage() {
}

int CommunicationStartAcknowledgeMessage::getMessageLength(){
	return Message::getMessageLength();
}

void CommunicationStartAcknowledgeMessage::readData(char **serializedMessageIndex){}
void CommunicationStartAcknowledgeMessage::writeData(char **serializedMessageIndex){}

} /* namespace RobotProtocol */
