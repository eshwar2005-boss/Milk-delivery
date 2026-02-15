# 🚀 Sangareddy Milk Delivery System - Deployment Guide

## Make Your Website Accessible to Everyone!

This guide will help you deploy your milk delivery system to the cloud so anyone can access it from anywhere.

---

## 📋 Deployment Options

### Option 1: **Render.com** (Recommended - FREE)

#### Step-by-Step Instructions:

1. **Create a Render Account**
   - Go to https://render.com
   - Sign up with GitHub, GitLab, or email

2. **Push Your Code to GitHub**
   ```powershell
   cd c:\Users\banda\Downloads\sangareddy-milk-system
   
   # If not already initialized
   git init
   git add .
   git commit -m "Ready for deployment"
   
   # Create a new repository on GitHub, then:
   git remote add origin https://github.com/YOUR_USERNAME/sangareddy-milk-system.git
   git branch -M main
   git push -u origin main
   ```

3. **Deploy on Render**
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Configure:
     - **Name**: sangareddy-milk
     - **Root Directory**: backend
     - **Build Command**: `mvn clean package -DskipTests`
     - **Start Command**: `java -jar target/milk-0.0.1-SNAPSHOT.jar`
     - **Instance Type**: Free
   - Click "Create Web Service"

4. **Your Site Will Be Live!**
   - URL: `https://sangareddy-milk.onrender.com`
   - It may take 5-10 minutes for first deployment

---

### Option 2: **Railway.app** (Also FREE with $5 credit)

#### Step-by-Step Instructions:

1. **Create a Railway Account**
   - Go to https://railway.app
   - Sign in with GitHub

2. **Deploy from GitHub**
   - Click "New Project" → "Deploy from GitHub repo"
   - Select your repository
   - Select the `backend` folder
   - Railway will auto-detect Maven and Java
   - Click "Deploy"

3. **Generate Domain**
   - Go to Settings → Generate Domain
   - Your site: `https://sangareddy-milk.up.railway.app`

---

### Option 3: **Fly.io** (FREE tier available)

#### Step-by-Step Instructions:

1. **Install Fly CLI**
   ```powershell
   powershell -Command "iwr https://fly.io/install.ps1 -useb | iex"
   ```

2. **Login and Launch**
   ```powershell
   cd c:\Users\banda\Downloads\sangareddy-milk-system\backend
   fly auth login
   fly launch
   ```

3. **Deploy**
   ```powershell
   fly deploy
   ```

---

## 🌐 Alternative: Make Local Server Accessible (Not Recommended for Production)

### Using ngrok (Temporary Public URL)

1. **Download ngrok**
   - Go to https://ngrok.com/download
   - Sign up and get auth token

2. **Run ngrok**
   ```powershell
   ngrok http 8080
   ```

3. **Share the URL**
   - ngrok will give you a public URL like: `https://abc123.ngrok.io`
   - Share this URL with anyone
   - **Note**: This URL changes every time you restart ngrok

---

## 🔧 Pre-Deployment Checklist

✅ **Files Created:**
- `Procfile` - Deployment command
- `system.properties` - Java version
- Your application is ready to deploy!

✅ **Database:**
- SQLite database will be included
- Consider upgrading to PostgreSQL for production

✅ **Security:**
- Add authentication/authorization for production
- Use environment variables for sensitive data
- Enable HTTPS (automatic on Render/Railway)

---

## 📱 After Deployment

### 1. **Update Landing Page URL**
Replace `localhost:8080` with your deployment URL in:
- landing.html
- dashboard.html  
- admin.html

### 2. **Share Your Website**
Once deployed, your milk delivery system will be accessible at:
- **Render**: `https://sangareddy-milk.onrender.com`
- **Railway**: `https://sangareddy-milk.up.railway.app`
- **Fly.io**: `https://sangareddy-milk.fly.dev`

### 3. **Custom Domain (Optional)**
- Buy a domain from Namecheap, GoDaddy, etc.
- Point it to your Render/Railway/Fly.io app
- Example: `www.sangared dymilk.com`

---

## 🆘 Troubleshooting

### Build Fails?
- Check Java version is 21
- Ensure `pom.xml` is correct
- Check Maven logs

### Database Issues?
- SQLite may not persist on free tiers
- Upgrade to PostgreSQL for production
- Add this to `pom.xml`:
  ```xml
  <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
  </dependency>
  ```

### Port Issues?
- Free tier platforms assign random ports
- Use `${PORT}` environment variable (already configured in Procfile)

---

## 🎉 You're Ready!

Choose one of the deployment options above and your milk delivery system will be live on the internet!

**Recommended**: Start with Render.com - it's the easiest and completely free.
