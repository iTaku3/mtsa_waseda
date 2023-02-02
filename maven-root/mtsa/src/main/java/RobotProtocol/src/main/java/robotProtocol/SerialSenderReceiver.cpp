/*
 * SerialSenderReceiver.cpp
 *
 *  Created on: Mar 27, 2013
 *      Author: mariano
 */

#include "SerialSenderReceiver.h"



namespace RobotProtocol {

SerialSenderReceiver::SerialSenderReceiver() {
	// TODO Auto-generated constructor stub
	messageLength	= 0;
	readBytes		= 0;
	length			= 0;
#ifdef UBRR1H
	#warning serial1 defined for serial sender receiver
  	serial			= &Serial1;
#else
	#warning serial defined for serial sender receiver
  	serial			= &Serial;
#endif
  	//serial->flush();
}

SerialSenderReceiver::~SerialSenderReceiver() {
	// TODO Auto-generated destructor stub
}

bool SerialSenderReceiver::hasPendingMessage(){
	if(length > 0)
		return true;

	bool messageComplete	= false;
	//consume input queue and add to read buffer
	while(serial->available() && readBytes < MAX_MESSAGE_LENGTH){
		messageBuffer[readBytes] = serial->read();
		readBytes++;
	}
	//serial->flush();

	//check if enough bytes were read to determine message length
	if(readBytes >= (Message::MESSAGE_HEADER_LENGTH)){
		bool shouldRejectBytes	= false;
		for(int i = 0; i <  Message::HEAD_FIELD_LENGTH; i++){
			//head does not matches expected value, trim buffer
			if(messageBuffer[i] != Message::MESSAGE_HEAD_VALUE[i]){
				shouldRejectBytes	= true;
				break;
			}
		}

		if(!shouldRejectBytes){
			//get length to see if enough bytes were read to have a complete message
			char lengthIndex[Message::LENGTH_FIELD_LENGTH + 1];
			lengthIndex[Message::LENGTH_FIELD_LENGTH] = '\0';
			memcpy(lengthIndex, (char*)(messageBuffer + Message::HEAD_FIELD_LENGTH)
					, Message::LENGTH_FIELD_LENGTH);
			length = (int)strtol(lengthIndex, NULL, 10);

			if(readBytes >= length){
				messageComplete	= true;
			}else{
				length = 0;
			}

		}else{
			//trim message
			//serial->flush();
			readBytes	= 0;
			length 		= 0;
		}

	}

	//check if read bytes conform a proper message

	return messageComplete && length > 0;
}

void SerialSenderReceiver::getNextMessage(char *incomingMessage, int &messageLength){
	this->messageLength		= 0;
	char	lastChar;


	//copy as much bytes as message declared length
	while(this->messageLength <= MAX_MESSAGE_LENGTH && this->messageLength < length){
		lastChar		= messageBuffer[this->messageLength];
		incomingMessage[this->messageLength]	= lastChar;
		this->messageLength++;
	}


	if(this->messageLength > MAX_MESSAGE_LENGTH){
		this->messageLength = 0;
	}

	/*
	if(length > 0 && readBytes > length){
		//copy tail to head
		for(int i = 0; i < (readBytes - length); i++)
			messageBuffer[i] = messageBuffer[length + i];

		readBytes			-= length;
	}
	*/

	readBytes			= 0;
	messageLength		= this->messageLength;
	length				= 0;
	//serial->flush();
}

bool SerialSenderReceiver::sendMessage(char *messageToSend, int messageToSendLength){
	bool success	= true;
	int currentIndex= 0;
	if((messageToSendLength <= 0) || (messageToSendLength > MAX_MESSAGE_LENGTH)){
		return false;
	}

	//serial->flush();

	messageLength	= messageToSendLength;

	while(currentIndex < messageToSendLength){
		serial->write(messageToSend[currentIndex]);
		currentIndex++;
	}
	delay(20);
	//serial->flush();
	return success;
}


} /* namespace RobotProtocol */
