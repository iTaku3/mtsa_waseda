################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.c 

OBJS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.o 

C_DEPS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.d 


# Each subdirectory must supply rules for building sources it contributes
src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/ConfigDescriptor.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/DeviceStandardReq.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/EndpointStream.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/Events.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/HostStandardReq.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/PipeStream.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/HighLevel/USBTask.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


