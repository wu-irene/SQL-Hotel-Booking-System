# Hotel Booking System

## Overview

This hotel booking system is a comprehensive database project implemented using PostgreSQL, designed as part of the CS166 Databases course. The project emphasizes practical SQL queries and database management, focusing on the real-world application of PostgreSQL in hotel management.

## Features

- **Browse Hotels**: Allows users to search for hotels based on distance criteria, utilizing SQL functions for distance checking.
- **View Rooms**: Enables viewing available rooms in a specific hotel, incorporating room availability checks against existing bookings.
- **Book Rooms**: Supports room booking with checks for room availability on desired dates, ensuring accurate booking operations.
- **View Recent Bookings**: Provides users with the ability to view their most recent room bookings, limited to the latest five for quick access.
- **Update Room Info**: Facilitates the updating of room information, including prices and URLs, with changes logged for record-keeping.
- **View Recent Updates**: Displays the five most recent updates to room information for a given hotel, aiding in operational transparency.
- **View Booking History of Hotel**: Allows hotel managers to view the booking history for their hotel, offering insights into occupancy trends.
- **View Regular Customers**: Identifies and ranks regular customers based on the frequency of bookings, excluding administrative bookings.
- **Place Room Repair Requests**: Manages the process of requesting repairs for rooms, including generating and tracking repair IDs.
- **View Room Repair History**: Provides a history of room repairs, supporting maintenance tracking and planning.

## Performance Tuning

To enhance the system's performance, I've implemented 8 indexes optimized for various queries involved in the system's operations. These indexes improve the efficiency of browsing hotels, viewing rooms, booking operations, and more.

