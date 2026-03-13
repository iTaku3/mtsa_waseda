################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.c \
../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.c 

OBJS += \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.o \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.o 

C_DEPS += \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Device.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Endpoint.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Host.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/Pipe.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBController.d \
./src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/USBInterrupt.d 


# Each subdirectory must supply rules for building sources it contributes
src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/%.o: ../src/DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/LowLevel/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/marianoc/Desktop/repos/mtsa-code/trunk/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


