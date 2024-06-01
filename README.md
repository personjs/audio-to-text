# Audio-To-Text
Process audio files to text offline using python <strong>speech_recognition</strong> library wrapped in a basic webapp.

## Getting Started
```bash
docker volume create uploads
docker run --rm --name audio-to-text -p 8080:8080 -v uploads:/app/uploads -dit yunostove/audio-to-text:latest
```