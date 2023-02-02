################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/Link\ to\ robotProtocol/messages/AnalogSubscribeMessage.cpp \
../src/Link\ to\ robotProtocol/messages/AnalogUnsubscribeMessage.cpp \
../src/Link\ to\ robotProtocol/messages/AnalogValueAcknowledgeMessage.cpp \
../src/Link\ to\ robotProtocol/messages/AnalogValueMessage.cpp \
../src/Link\ to\ robotProtocol/messages/CommandAcceptedMessage.cpp \
../src/Link\ to\ robotProtocol/messages/CommandErrorMessage.cpp \
../src/Link\ to\ robotProtocol/messages/CommunicationStartAcknowledgeMessage.cpp \
../src/Link\ to\ robotProtocol/messages/CommunicationStartMessage.cpp \
../src/Link\ to\ robotProtocol/messages/CommunicationStopMessage.cpp \
../src/Link\ to\ robotProtocol/messages/SetAnalogValueMessage.cpp 

OBJS += \
./src/Link\ to\ robotProtocol/messages/AnalogSubscribeMessage.o \
./src/Link\ to\ robotProtocol/messages/AnalogUnsubscribeMessage.o \
./src/Link\ to\ robotProtocol/messages/AnalogValueAcknowledgeMessage.o \
./src/Link\ to\ robotProtocol/messages/AnalogValueMessage.o \
./src/Link\ to\ robotProtocol/messages/CommandAcceptedMessage.o \
./src/Link\ to\ robotProtocol/messages/CommandErrorMessage.o \
./src/Link\ to\ robotProtocol/messages/CommunicationStartAcknowledgeMessage.o \
./src/Link\ to\ robotProtocol/messages/CommunicationStartMessage.o \
./src/Link\ to\ robotProtocol/messages/CommunicationStopMessage.o \
./src/Link\ to\ robotProtocol/messages/SetAnalogValueMessage.o 

CPP_DEPS += \
./src/Link\ to\ robotProtocol/messages/AnalogSubscribeMessage.d \
./src/Link\ to\ robotProtocol/messages/AnalogUnsubscribeMessage.d \
./src/Link\ to\ robotProtocol/messages/AnalogValueAcknowledgeMessage.d \
./src/Link\ to\ robotProtocol/messages/AnalogValueMessage.d \
./src/Link\ to\ robotProtocol/messages/CommandAcceptedMessage.d \
./src/Link\ to\ robotProtocol/messages/CommandErrorMessage.d \
./src/Link\ to\ robotProtocol/messages/CommunicationStartAcknowledgeMessage.d \
./src/Link\ to\ robotProtocol/messages/CommunicationStartMessage.d \
./src/Link\ to\ robotProtocol/messages/CommunicationStopMessage.d \
./src/Link\ to\ robotProtocol/messages/SetAnalogValueMessage.d 


# Each subdirectory must supply rules for building sources it contributes
src/Link\ to\ robotProtocol/messages/AnalogSubscribeMessage.o: ../src/Link\ to\ robotProtocol/messages/AnalogSubscribeMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/AnalogSubscribeMessage.d" -MT"src/Link\ to\ robotProtocol/messages/AnalogSubscribeMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/AnalogUnsubscribeMessage.o: ../src/Link\ to\ robotProtocol/messages/AnalogUnsubscribeMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/AnalogUnsubscribeMessage.d" -MT"src/Link\ to\ robotProtocol/messages/AnalogUnsubscribeMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/AnalogValueAcknowledgeMessage.o: ../src/Link\ to\ robotProtocol/messages/AnalogValueAcknowledgeMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/AnalogValueAcknowledgeMessage.d" -MT"src/Link\ to\ robotProtocol/messages/AnalogValueAcknowledgeMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/AnalogValueMessage.o: ../src/Link\ to\ robotProtocol/messages/AnalogValueMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/AnalogValueMessage.d" -MT"src/Link\ to\ robotProtocol/messages/AnalogValueMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/CommandAcceptedMessage.o: ../src/Link\ to\ robotProtocol/messages/CommandAcceptedMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/CommandAcceptedMessage.d" -MT"src/Link\ to\ robotProtocol/messages/CommandAcceptedMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/CommandErrorMessage.o: ../src/Link\ to\ robotProtocol/messages/CommandErrorMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/CommandErrorMessage.d" -MT"src/Link\ to\ robotProtocol/messages/CommandErrorMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/CommunicationStartAcknowledgeMessage.o: ../src/Link\ to\ robotProtocol/messages/CommunicationStartAcknowledgeMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/CommunicationStartAcknowledgeMessage.d" -MT"src/Link\ to\ robotProtocol/messages/CommunicationStartAcknowledgeMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/CommunicationStartMessage.o: ../src/Link\ to\ robotProtocol/messages/CommunicationStartMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/CommunicationStartMessage.d" -MT"src/Link\ to\ robotProtocol/messages/CommunicationStartMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/CommunicationStopMessage.o: ../src/Link\ to\ robotProtocol/messages/CommunicationStopMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/CommunicationStopMessage.d" -MT"src/Link\ to\ robotProtocol/messages/CommunicationStopMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ robotProtocol/messages/SetAnalogValueMessage.o: ../src/Link\ to\ robotProtocol/messages/SetAnalogValueMessage.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to robotProtocol/messages/SetAnalogValueMessage.d" -MT"src/Link\ to\ robotProtocol/messages/SetAnalogValueMessage.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


