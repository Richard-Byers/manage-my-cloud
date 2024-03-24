# Step 1: Specify a base image
FROM node:20-alpine

# Step 2: Set the working directory in the Docker image
WORKDIR /app

# Step 3: Copy package.json and package-lock.json (if available)
COPY package.json ./

# Step 4: Install dependencies
RUN npm install

# Step 5: Copy local code to the container image
COPY . .

ARG REACT_APP_ENV=production

ENV REACT_APP_ENV $REACT_APP_ENV

# Step 6: Build the application
RUN npm run build

# Step 7: Install serve
RUN npm install -g serve

# Step 8: Expose the port server is running on
EXPOSE 3000

# Step 9: Run the application
CMD ["serve", "-s", "build", "-l", "3000"]