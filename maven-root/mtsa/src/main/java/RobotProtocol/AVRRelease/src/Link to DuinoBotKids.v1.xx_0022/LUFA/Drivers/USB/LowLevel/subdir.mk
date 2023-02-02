################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.c 

OBJS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.o 

C_DEPS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.d 


# Each subdirectory must supply rules for building sources it contributes
src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


