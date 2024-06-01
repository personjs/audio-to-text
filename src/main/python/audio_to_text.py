import sys
import speech_recognition as sr
from moviepy.editor import AudioFileClip

def convert_m4a_to_wav(input_file, output_file):
    try:
        audio_clip = AudioFileClip(input_file)
        audio_clip.write_audiofile(output_file)
    except Exception as e:
        print("Error during conversion:", str(e))
        exit(1)

def audio_to_text(audio_file):
    recognizer = sr.Recognizer()
    
    # Load audio file
    with sr.AudioFile(audio_file) as source:
        audio_data = recognizer.record(source)
    
    # Convert audio to text using CMU Sphinx
    try:
        text = recognizer.recognize_sphinx(audio_data)
        return text
    except sr.UnknownValueError:
        print("Sphinx could not understand the audio.")
    except sr.RequestError as e:
        print(f"Sphinx error; {e}")
    
    return None

# Example usage
# if len(sys.argv) > 1:
#     audio_file = sys.argv[1]
# else:
#     audio_file = 'test.m4a'
audio_file = sys.argv[1]

if audio_file.endswith(".m4a"):
    output_file = audio_file.replace('.m4a', '.wav')
    convert_m4a_to_wav(audio_file, output_file)
    audio_file = output_file
    
text = audio_to_text(audio_file)
if text:
    print(text)
